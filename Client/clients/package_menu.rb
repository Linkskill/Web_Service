require 'tty-prompt'
require_relative '../modules/web_service.rb'
require_relative '../modules/validate_user_entry.rb'
require_relative '../entities/package_entity.rb'

# Menu com opções (listagem, pesquisa e compra) para os pacotes
class PackageMenu
    def initialize
        @prompt = TTY::Prompt.new
    end

    # Exibe a tela de menu e as opções
    def display_menu
        while true
            @prompt.say("\n\t→Pacotes de viagem←")
            chosen_option = @prompt.select("Escolha uma opção:", echo:false) do |menu|
                menu.enum '.'
                menu.choice 'Listar os pacotes disponíveis', :show_all
                menu.choice 'Pesquisar por pacote específico', :search
                menu.choice 'Comprar um pacote', :buy
                menu.choice 'Voltar', :back
            end

            case chosen_option
            when :show_all
                show_packages
            when :search
                search_package
            when :buy
                buy_package
            when :back
                return
            end
        end
    end

    private
        # Exibe na tela todas os pacotes do servidor
        def show_packages
            begin
                packages = WebService.get_packages
            rescue => e
                @prompt.error(e.message)
                return
            end

            packages_length = packages.length
            if packages_length > 0
                @prompt.say("\n#{packages_length} pacotes encontrados")
                @prompt.say("\nListando pacotes:\n")

                packages.each do |package|
                    print(package.string_converter)
                end
            else
                @prompt.say("\nNenhum pacote foi encontrado")
            end
        end

        # Busca por pacotes semelhantes de acordo com a entrada do usuário
        def search_package
            @prompt.say("Insira os campos a seguir para buscar o pacote desejado.")
            ticket_type = @prompt.select("Tipo da passagem: ", ["ida e volta", {
                name: "somente ida", disabled: "Passagens precisam ser de ida e volta"}])
            origin = @prompt.ask("Local de origem: ", convert: :string) do |e|
                e.required(true, 'Campo vazio, preencha')
            end
            hotel_or_city_name = @prompt.ask("Nome do hotel ou da cidade: ", convert: :string) do |e|
                e.required(true, 'Campo vazio, preencha')
            end
            num_rooms = @prompt.ask("Número de quartos: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end
            num_people =  @prompt.ask("Número de pessoas: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end
            departure_date = @prompt.ask("Data de partida(dia-mês-ano): ", convert: :date) do |e|
                e.required(true, 'Campo vazio, preencha')
                e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formato "dia-mês-ano"')
            end
            return_date = 
                if ticket_type == "ida e volta" 
                    @prompt.ask("Data de retorno(dia-mês-ano): ", convert: :date) do |e|
                        e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formado "dia-mês-ano"')
                    end
                else
                    nil
                end

            begin
                similar_packages = WebService.get_similar_packages(
                    ticket_type: ticket_type,
                    origin: origin,
                    hotel_or_city_name: hotel_or_city_name,
                    departure_date: departure_date,
                    return_date: return_date,
                    num_rooms: num_rooms,
                    num_people: num_people
                )
            rescue => e
                @prompt.error(e.message)
                return
            end

            similar_packages_length = similar_packages.length
            if similar_packages_length > 0
                @prompt.say("\n#{similar_packages_length} pacotes encontrados")
                
                similar_packages.each do |package|
                    print(package.string_converter)
                end
            else
                @prompt.say("\nNenhum pacote foi encontrado")
            end
        end

        # Compra um pacote de acordo com a entrada do usuário
        def buy_package
            @prompt.say("Insira os campos a seguir para comprar um pacote.")
            package_id = @prompt.ask("Identificador da pacote: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end

            confirmation = @prompt.yes?("Deseja comprar o pacote ##{package_id}?")
            if confirmation == false 
                @prompt.("Compra cancelada")
                return
            end

            begin
                response = WebService.buy_package(package_id)
                @prompt.ok(response)
            rescue => e
                @prompt.error(e.message)
            end
        end

end